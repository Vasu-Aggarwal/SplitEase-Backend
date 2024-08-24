package com.vr.SplitEase.dto.request;

import com.vr.SplitEase.dto.response.GptMessage;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GptRequest {
    private String model;
    private List<GptMessage> messages;
    private Integer max_tokens;
    private Integer n;

    public GptRequest(String model, String prompt){
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new GptMessage("system", "User will provide a sentence might consist one word or more than one word. Currently the user belong from INDIA only. Your job is to always provide a single word response which is best suited category from the text provided. Given below is the category list, use this list only to detect the category if not found then return general:\\nGeneral\\nGames\\nMovies\\nMusic\\nSports\\nOther_Entertainment\\nGroceries\\nDining Out\\nOther_Food\\nLiquor\\nRent\\nMortgage\\nHousehold supplies\\nFurniture\\nMaintenance\\nOther_Home\\nPets\\nServices\\nElectronics\\nInsurance\\nClothing\\nGifts\\nMedical expenses\\nOther_Life\\nTaxes\\nEducation\\nChildcare\\nParking\\nCar\\nBus/train\\nGas/fuel\\nOther_Transportation\\nPlane\\nTaxi\\nHotel\\nElectricity\\nHeat/gas\\nWater\\nTV/phone/internet\\nOther_Utilities\\nCleaning"));
        this.messages.add(new GptMessage("user", prompt));
        this.max_tokens = 1;
        this.n = 1;
    }
}
