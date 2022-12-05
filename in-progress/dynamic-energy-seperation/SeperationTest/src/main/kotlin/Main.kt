import kotlin.math.absoluteValue

fun main() {
    energyDistribution(
        11.0, listOf(
            Member(0.0, 1.0),
            Member(1.0, 0.0),
            Member(2.0, -1.0),
            Member(12.0, -3.0),
        )
    )
}

private fun energyDistribution(feedIn: Double, members: List<Member>) {
    if(feedIn > 0) {
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
                    }
                } else {
                    println("surplus >= flexibility --> maybe more feed in than load")
                    members.forEach {
                        if (it.flexibility > 0) it.deviation = it.flexibility
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
                    }
                } else {
                    println("difference > flexibility")
                    // TODO
                    members.forEach {
                        it.deviation = -difference * it.load / sumLoad
                        if (it.deviation < 0 && it.deviation > it.flexibility) {
                            it.deviation = it.flexibility
                        }
                    }
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
        println("L: ${it.load}; F: ${it.flexibility}; Z: ${it.load + it.deviation}")
    }
}