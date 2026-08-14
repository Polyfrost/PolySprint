plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2" /* [SC] DO NOT EDIT */

stonecutter tasks {
    order("publishModrinth")
}

stonecutter parameters {
    replacements {
        string(current.parsed < "26.1") {
            replace("classTweaker v1 official", "classTweaker v1 named")
        }
    }
}
