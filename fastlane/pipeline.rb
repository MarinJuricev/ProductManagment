require 'yaml'
import './config.rb'
import './utility.rb'

# List of all acceptable parameters for this lane
desc "Parameters"
desc "build_type: build type that will be used for variant resolution. Options: Config::BUILD_TYPE_DEBUG, Config::BUILD_TYPE_RELEASE"
desc "build_task: build_task that will be used to create apk or aab. Options: Config::BUILD_TASK_ASSEMBLE, Config::BUILD_TASK_BUNDLE"
desc "distribute_by_firebase_distribution: should app be deployed on Firebase distribution. Options: (true|false default: false)"
desc "firebase_app_id: defines this specific Firebase app id (required if 'distribute_by_firebase_distribution'=true)"
desc "qa: should code quality check be performed (true|false default: false)"
desc "unit_tests: run unit tests (true|false default: false)"
desc "slack: should informative message about build be posted to Slack. Options: (true|false default: false)"
desc "version_bump: if present version will be bumped with defined type. Options: Config::BUMP_BUILD, Config::BUMP_PATCH, Config::BUMP_MINOR, Config::BUMP_MAJOR"
desc "vcs: should changes made by pipeline be committed and pushed to VCS. Options: (true|false default: false)"
desc "environment: environment that app should target. Options: Config::ENVIRONMENT_DEV, Config::ENVIRONMENT_PROD"
desc "testers_group: group of testers. Options: Config::TESTER_GROUP_INTERNAL, Config::TESTER_GROUP_LIVE_USERS"

private_lane :pipeline do |options|
  # If some parameters are required best practice is to check early if something is missing
  UI.user_error!("Parameter [build_type] is required!") unless [Config::BUILD_TYPE_DEBUG, Config::BUILD_TYPE_RELEASE].include? options[:build_type]
  UI.user_error!("Parameter [build_task] is required!") unless [Config::BUILD_TASK_ASSEMBLE, Config::BUILD_TASK_BUNDLE].include? options[:build_task]
  UI.user_error!("Parameter [environment] is required!") unless [Config::ENVIRONMENT_DEV, Config::ENVIRONMENT_PROD].include? options[:environment]

  # make sure that all changes are committed before build
  ensure_git_status_clean

  # CI will checkout specific commit (detached HEAD). Therefore, we need to checkout branch and pull last known changes from remote
  if ENV["CI"] == "true"
    UI.message "[INFO] Running on CI environment."
    UI.message "[INFO] Performing git checkout for branch: #{ENV["CI_BUILD_REF_NAME"]}"
    sh "git fetch --all"
    sh "git checkout #{ENV["CI_BUILD_REF_NAME"]}"
    sh "git status"
    sh "git pull"
  end

  if options[:qa]
    gradle(task: "ktlintCheck detekt")     # Run quality checks
  end

  if options[:unit_tests]
    gradle(task: "test")     # Run unit tests
  end

  # clean build directory
  gradle(task: 'clean')

  # bump build version (ensure default)
   if !options[:version_bump].nil?
     bump_type = options[:version_bump]
     UI.message "[INFO] bumping version with type: #{bump_type}"
     gradle(task: bump_type)
   end

  version_name = read_version_name

  # extract app variants and flavors
  application = options[:application]
  targets = options[:targets]
  environment = options[:environment]
  build_type = options[:build_type]
  build_task_type = options[:build_task]
  testers = options[:testers_group]

  # Populate the buildTasks array with the correct format
  buildTasks = ["#{build_task_type}#{environment.capitalize()}#{build_type.capitalize}"]

  UI.message "[INFO] Building tasks: #{buildTasks}"

  # run predefined gradle build tasks
  buildTasks.each { |task|
    gradle(task: task)
  }

  # distribute via Firebase distribution
  if options[:distribute_by_firebase_distribution]
    # resolve firebase app distribution app identifier based on build_type
    firebase_distribution_app = options[:firebase_app_id]
    UI.user_error!("Firebase app id is required but null provided") unless !firebase_distribution_app.nil?

    # find apps generated few blocks before, check laneContext for more lane variables https://docs.fastlane.tools/advanced/lanes/#lane-context
    output_paths = Actions.lane_context[SharedValues::GRADLE_ALL_APK_OUTPUT_PATHS]
    output_paths.each { |apk_path|
      # upload build to Firebase App Distribution and share it with tester group
      UI.message "[INFO] APK PATH: #{apk_path}"
      firebase_app_distribution(
        apk_path: apk_path,
        app: firebase_distribution_app,
        groups: testers,
      )
    }
  end

  # update vcs with changes made by this pipeline
    if options[:vcs]
      # commit version bump and changelog changes to git
      git_commit(
        path: [relative_from_project_root(Config::VERSIONING_FILE_PATH),],
        # [ci skip] in a commit message will skip the CI pipeline for that commit
        message: "Build Version Bump for version: " + version_name + " [ci skip]"
      )

      #Check if CI is running on BITRISE, Gitlab or locally.
      #If running on Bitrise or locally lane can use default property for git remote, but Gitlab-ci
      #for some reason has only read access, so we need to create git remote url with ci token.
      git_remote = ENV["CI_TOKEN"] ? "https://CI:#{ENV["CI_TOKEN"]}#{Config::GIT_REPO_TOKEN_URL_SUFFIX}" : Config::GIT_REPO
      # push commits to remote branch
      push_to_git_remote(
        local_branch: git_branch,
        remote_branch: git_branch,
      )
    end

    # Reset git repo to a clean state, discarding any uncommitted and untracked changes.
    # If vcs flag is used required files are committed and paused prior to this command.
    # All other files will be reset by this command (e.g. app icons changed by badge)
    # skip_clean ('git clean') to avoid removing untracked files like `.properties`
    reset_git_repo(skip_clean: true)

  if options[:slack]
      # resolve app name that will be posted on Slack
      slack(
        slack_url: Config::SLACK_URL,
        message: "New android build is available! :rocket:",
        payload: {
          'App' => Config::SLACK_APP_NAME,
          'Version' => version_name,
          'Environment' => environment,
          'Build Date' => Time.new.to_s,
          'Built by' => 'Fastlane',
          'Check available builds' => Config::FIREBASE_DISTRIBUTION_URL
        },
        default_payloads: [:git_branch, :git_author]
      )
    end
end