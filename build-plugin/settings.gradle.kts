@Suppress(names = ["UnstableApiUsage"])
dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("../deps.versions.toml"))
        }
    }
}

include(":plugin")
