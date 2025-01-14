package core.model

enum class Environment(val value: String) {
    PRODUCTION(production),
    DEVELOPMENT(development),
    ;

    companion object {
        fun fromString(value: String?): Environment? = entries.find { it.value == value }
    }
}

fun environmentFromString(value: String?): Environment? = Environment.fromString(value)

private const val production = "production"
private const val development = "development"
