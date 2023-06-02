namespace e_community_cloud_lib.Endpoints.Dtos;

public class BlockchainAccountBalanceDto
{
    /// <summary>
    /// received ETH from other members
    /// </summary>
    public string Received { get; set; }
    
    /// <summary>
    /// sent/payed ETH to other members
    /// </summary>
    public string Sent { get; set; }
    
    /// <summary>
    /// current balance of the ethereum account
    /// </summary>
    public string Balance { get; set; }
}