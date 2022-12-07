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
        val sumFlexibilityPlus = members.filter { it.flexibility > 0 }.sumOf { it.flexibility }
        val sumFlexibilityMinus = -members.filter { it.flexibility < 0 }.sumOf { it.flexibility }
        println("### E: $feedIn; L: $sumLoad; F+: $sumFlexibilityPlus; F-: $sumFlexibilityMinus ###")

        if (difference > 0) {
            println("PLUS")
            if (sumFlexibilityPlus > 0) {
                println("FLEXIBILITY")
                if (difference < sumFlexibilityPlus) {
                    println("surplus < flexibility --> percentage")
                    members.forEach {
                        if (it.flexibility > 0) it.deviation = difference * it.flexibility / sumFlexibilityPlus
                        it.optimized = true
                    }
                } else {
                    println("surplus >= flexibility --> maybe more feed in than load")
                    members.forEach {
                        if (it.flexibility > 0) it.deviation = it.flexibility
                        it.optimized = true
                    }
                }
            } else {
                println("more feed in than load")
            }
        } else if (difference < 0) {
            println("MINUS")
            if (sumFlexibilityMinus > 0) {
                println("FLEXIBILITY")
                if (-difference <= sumFlexibilityMinus) {
                    println("difference <= flexibility --> percentage")
                    members.forEach {
                        if (it.flexibility < 0) it.deviation = -difference * it.flexibility / sumFlexibilityMinus
                        it.optimized = true
                    }
                } else {
                    println("difference > flexibility")

                    // separate evenly
                    members.forEach {
                        it.deviation = difference * it.load / sumLoad
                    }

                    var openMembers = members.size

                    fun optimize() {
                        var shouldOptimizeAgain = false
                        var openLoad = 0.0

                        members.forEach {
                            if (!it.optimized && it.flexibility < 0 && it.flexibility < it.deviation) {
                                // member gets more than he actually needs
                                openLoad += (it.deviation - it.flexibility)
                                it.deviation = it.flexibility
                                it.optimized = true
                                openMembers--
                                shouldOptimizeAgain = true
                            }
                        }

                        members.forEach {
                            if(!it.optimized) it.deviation += openLoad * 1 / openMembers
                        }
                        if (shouldOptimizeAgain) {
                            optimize()
                        }
                    }
                    optimize()
                }
            } else {
                println("evenly distribute load difference")
                members.forEach {
                    it.deviation = -difference * it.load / sumLoad
                }
            }
        }
    }

    members.forEach {
        println("L: ${it.load}; F: ${it.flexibility}; Z: ${it.load + it.deviation}; O: ${it.optimized}")
    }
}