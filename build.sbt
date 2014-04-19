startOptions <<= (startOptions, bluePack) map { (opt, pack) => "-cwd" :: pack.getCanonicalPath :: opt }

launchExe := "/usr/bin/jsvc"

