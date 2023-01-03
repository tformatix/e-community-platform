package at.fhooe.ecommunity.ui.screen.sharing


data class ConsentContract(
    var startEnergyDataDate: String,
    var endEnergyDataDate: String,
    var contractValidityDate: String,
    var pricePerOneHour: String,
    var numberOfHours: Long = 0,
    var priceSummary: String
) {
    fun isReadyForSubmit(): Boolean {
        return startEnergyDataDate.isNotEmpty() &&
                endEnergyDataDate.isNotEmpty() &&
                contractValidityDate.isNotEmpty() &&
                pricePerOneHour.isNotEmpty()
    }
}