
class Config

    #Environment
    ENVIRONMENT_DEV = "dev"
    ENVIRONMENT_PROD = "prod"

    # Build type
    BUILD_TYPE_DEBUG = "debug"
    BUILD_TYPE_RELEASE = "release"

    # Gradle build task type
    BUILD_TASK_ASSEMBLE = "assemble"
    BUILD_TASK_BUNDLE = "bundle"

    # Path to generated versioning file (version name)
    VERSIONING_FILE_PATH = "../release/version.properties"

    # Version bump tasks
    BUMP_BUILD = "composeApp:incrementBuild"
    BUMP_PATCH = "composeApp:incrementPatch"
    BUMP_MINOR = "composeApp:incrementMinor"
    BUMP_MAJOR = "composeApp:incrementMajor"

    # Firebase production app distribution application identifier.
    FIREBASE_APP_ID = "1:988137395439:android:a4ad1654004cd2a24d771a"
    # Firebase development app distribution application identifier.
    FIREBASE_DEV_APP_ID = "1:988137395439:android:c121929c356e35cf4d771a"
    # Firebase distribution bucket url
    FIREBASE_DISTRIBUTION_URL = "https://appdistribution.firebase.google.com/testerapps"

    # Project repository git url
    GIT_REPO = "git@github.com:product/product-inventory-kmp.git"
    GIT_REPO_TOKEN_URL_SUFFIX = "@github.com/product/product-inventory-kmp.git"

    # Slack
    SLACK_URL = "https://hooks.slack.com/services/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"
    SLACK_CHANNEL = "#pr-inventory-app-int"
    SLACK_APP_NAME = "[Android] product Inventory App"

    TESTER_GROUP_INTERNAL = "productQA"
    TESTER_GROUP_LIVE_USERS = "productLiveUsers"

end