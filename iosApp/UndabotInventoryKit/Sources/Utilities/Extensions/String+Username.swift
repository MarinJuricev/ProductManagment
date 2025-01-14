extension String {
    public var username: String {
        let delimiter = "@"
        return self.components(separatedBy: delimiter).first ?? ""
    }
}
