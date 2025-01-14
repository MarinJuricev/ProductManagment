require './config.rb'

desc "Read version name from versioning file"
private_lane :read_version_name do
  properties = {}
  File.readlines(Config::VERSIONING_FILE_PATH).each do |line|
    key, value = line.chomp.split '=', 2
    properties[key] = value
  end
  properties["VERSION_NAME"]
end

def relative_from_project_root(path)
  return Pathname.new(path).relative_path_from(Pathname.new("..")).to_s
end