package uk.matvey.app

enum class Profile {

    TEST,
    LOCAL,
    PROD,
    ;

    companion object {

        fun from(name: String) = Profile.valueOf(name.uppercase())
    }
}
