package moe.ofs.backend.discipline.service;

import moe.ofs.backend.discipline.exceptions.ValidatorNotSpecifiedException;
import moe.ofs.backend.discipline.model.ConnectionValidator;

public interface PlayerConnectionValidationService {
    void addValidator(ConnectionValidator validator) throws ValidatorNotSpecifiedException;

    void removeValidator(ConnectionValidator validator);

    void removeValidatorByName(String name);

}
