class ConsentContract:
    """ object for a consent contract (stored on the blockchain) """

    def __init__(self, address_proposer, address_consenter, contract_id, start_e_data, end_e_data,
                 validity_contract, price_hour, price_total, state, data_usage, time_resolution):
        self.address_proposer = address_proposer
        self.address_consenter = address_consenter
        self.contract_id = contract_id
        self.start_e_data = start_e_data
        self.end_e_data = end_e_data
        self.validity_contract = validity_contract
        self.price_hour = price_hour
        self.price_total = price_total
        self.state = state
        self.data_usage = data_usage
        self.time_resolution = time_resolution

    def to_dict(self):
        return {"ContractId": self.contract_id,
                "State": self.state,
                "AddressProposer": self.address_proposer,
                "AddressConsenter": self.address_consenter,
                "StartEnergyData": self.start_e_data,
                "EndEnergyData": self.end_e_data,
                "ValidityOfContract": self.validity_contract,
                "PricePerHour": self.price_hour,
                "TotalPrice": self.price_total,
                "DataUsage": self.data_usage,
                "TimeResolution": self.time_resolution
                }
