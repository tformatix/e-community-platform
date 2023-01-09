class ConsentContract:
    """ object for a consent contract (stored on the blockchain) """

    def __init__(self, address_proposer, address_consenter, contract_id, start_e_data, end_e_data,
                 validity_contract, price_hour, price_total):
        self.address_proposer = address_proposer
        self.address_consenter = address_consenter
        self.contract_id = contract_id
        self.start_e_data = start_e_data
        self.end_e_data = end_e_data
        self.validity_contract = validity_contract
        self.price_hour = price_hour
        self.price_total = price_total

    def to_dict(self):
        return {"address_proposer": self.address_proposer,
                "address_consenter": self.address_consenter,
                "contract_id": self.contract_id,
                "start_e_data": self.start_e_data,
                "end_e_data": self.end_e_data,
                "validity_contract": self.validity_contract,
                "price_hour": self.price_hour,
                "price_total": self.price_total}
