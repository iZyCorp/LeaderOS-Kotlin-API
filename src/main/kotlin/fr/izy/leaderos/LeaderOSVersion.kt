package fr.izy.leaderos

/**
 * Version of the LeaderOS instance the wrapper talks to.
 *
 * Endpoints that changed between LeaderOS releases are routed according to this value, so the same
 * call (e.g. `leaderOS.credits.add(...)`) works whatever the version of the website.
 */
data class LeaderOSVersion(
    val major: Int,
    val minor: Int = 0,
    val patch: Int = 0
) : Comparable<LeaderOSVersion> {

    override fun compareTo(other: LeaderOSVersion): Int =
        compareValuesBy(this, other, { it.major }, { it.minor }, { it.patch })

    override fun toString(): String = "$major.$minor.$patch"

    companion object {
        val V5_5_9 = LeaderOSVersion(5, 5, 9)

        /**
         * First release of the reworked API (credits, users, store listing and buy endpoints changed).
         * Every version below is routed to the legacy endpoints.
         */
        val V6_0_0 = LeaderOSVersion(6, 0, 0)

        val V6_3_3 = LeaderOSVersion(6, 3, 3)

        /**
         * Latest version this wrapper has been written against.
         */
        val LATEST = V6_3_3

        /**
         * Parses a version like `6.3.3`, `6.3` or `6`.
         */
        fun parse(version: String): LeaderOSVersion {
            val parts = version.trim().removePrefix("v").split('.')
            require(parts.size in 1..3) { "Invalid LeaderOS version: $version" }
            val numbers = parts.map { it.toIntOrNull() ?: throw IllegalArgumentException("Invalid LeaderOS version: $version") }
            return LeaderOSVersion(numbers[0], numbers.getOrElse(1) { 0 }, numbers.getOrElse(2) { 0 })
        }
    }
}
