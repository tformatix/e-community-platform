using System;

namespace e_community_local_lib.CloudData;

public class ConsentContractDto
{
    public Guid? ContractId { get; set; }
    public int State { get; set; }
    public string AddressContract { get; set; }
    public string AddressProposer { get; set; }
    public string AddressConsenter { get; set; }
    public string StartEnergyData { get; set; }
    public string EndEnergyData { get; set; }
    public string ValidityOfContract { get; set; }
    public string PricePerHour { get; set; }
    public string TotalPrice { get; set; }
}