import kotlin.math.absoluteValue

fun main() {
    energyDistribution(
        10.0, listOf(
            Member(2.0, 1.0),
            Member(3.0, 0.0),
            Member(4.0, -3.0),
            Member(5.0, -2.0),
            Member(6.0, -3.0),
        )
    )
}

private fun energyDistribution(feedIn: Double, members: List<Member>) {
    if (feedIn > 0) {
        val sumLoad = members.sumOf { it.load }
        val difference = feedIn - sumLoad
        println("### E: $feedIn; L: $sumLoad ###")

        if (difference > 0) {
            println("PLUS")
            energyDistributionPlus(members, difference)
        } else if (difference < 0) {
            println("MINUS")
            energyDistributionMinus(members, difference, sumLoad)
        }
    } else {
        println("nothing available")
        members.forEach {
            it.deviation = -it.load
        }
    }

    members.forEach {
        println("L: ${it.load}; F: ${it.flexibility}; Z: ${it.load + it.deviation}; O: ${it.optimized}")
    }
}

private fun energyDistributionPlus(members: List<Member>, difference: Double) {
    val sumFlexibilityPlus = members.filter { it.flexibility > 0 }.sumOf { it.flexibility }
    if (sumFlexibilityPlus > 0) {
        println("FLEXIBILITY")
        if (difference < sumFlexibilityPlus) {
            println("difference < flexibility --> % increase")
            members.forEach {
                if (it.flexibility > 0) it.deviation = difference * it.flexibility / sumFlexibilityPlus
                it.optimized = true
            }
        } else {
            println("difference >= flexibility --> maybe more feed in than load")
            members.forEach {
                if (it.flexibility > 0) it.deviation = it.flexibility
                it.optimized = true
            }
        }
    } else {
        println("more feed in than load")
    }
}

private fun energyDistributionMinus(
    members: List<Member>,
    difference: Double,
    sumLoad: Double
) {
    val sumFlexibilityMinus = -members.filter { it.flexibility < 0 }.sumOf { it.flexibility }
    if (sumFlexibilityMinus > 0) {
        println("FLEXIBILITY")
        if (-difference <= sumFlexibilityMinus) {
            println("difference <= flexibility --> % decrease based on flexibility")
            members.forEach {
                if (it.flexibility < 0) it.deviation = -difference * it.flexibility / sumFlexibilityMinus
                it.optimized = true
            }
        } else {
            println("difference > flexibility")

            // 1. separate evenly
            members.forEach {
                it.deviation = difference * it.load / sumLoad // % distribute load difference
            }

            negativeFlexibilityOptimization(members)
        }
    } else {
        println("evenly distribute load difference")
        members.forEach {
            it.deviation = -difference * it.load / sumLoad
        }
    }
}

/**
 * recursive function for optimization (difference > flexibility)
 * Example:
 * - feed in: 5 kWh
 * - sum load: 10 kWh
 *      - Member 1: 2 kWh + 1 kWh
 *      - Member 2: 3 kWh + 0 kWh
 *      - Member 3: 5 kWh + -3 kWh
 * - separation (feed in * load member / sum load)
 *      - Member 1: 1 kWh
 *      - Member 2: 1.5 kWh
 *      - Member 3: 2.5 kWh --> OPTIMIZATION (2 kWh would be also enough through the flexibility)
 * - OPTIMIZATION
 *      - Does every member needs it's assigned load?
 *      - If no: separate the opened load (0.5 kWh in example above) to the other members
 *      - REPEAT
 */
fun negativeFlexibilityOptimization(members: List<Member>) {
    var openMembers = members.size // hom many members are not optimized

    fun optimize() {
        var openLoad = 0.0 // how many load is open (through flexibility members)

        members.forEach {
            if (!it.optimized && it.flexibility < 0 && it.flexibility < it.deviation) {
                // member gets more than he actually needs
                openLoad += (it.deviation - it.flexibility)
                it.deviation = it.flexibility
                it.optimized = true // no more changes
                openMembers--
            }
        }

        if (openLoad > 0) {
            members.forEach {
                if (!it.optimized) it.deviation += openLoad * 1 / openMembers
            }
            optimize()
        }
    }

    optimize()
}