package uk.matvey.app

enum class Profile {

    TEST,
    LOCAL,
    PROD,
    ;

    fun isProd() = this == PROD

    companion object {

        fun from(name: String) = Profile.valueOf(name.uppercase())
    }
}
