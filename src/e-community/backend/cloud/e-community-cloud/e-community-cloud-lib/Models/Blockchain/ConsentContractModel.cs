using System;

namespace e_community_cloud_lib.Models.Blockchain;

public class ConsentContractModel
{
    public string ContractId { get; set; }
    public int State { get; set; }
    public string AddressContract { get; set; }
    public string AddressProposer { get; set; }
    public string AddressConsenter { get; set; }
    public string StartEnergyData { get; set; }
    public string EndEnergyData { get; set; }
    public string ValidityOfContract { get; set; }
    public string PricePerHour { get; set; }
    public string TotalPrice { get; set; }
    public string DataUsage { get; set; }
    public string TimeResolution { get; set; }
}