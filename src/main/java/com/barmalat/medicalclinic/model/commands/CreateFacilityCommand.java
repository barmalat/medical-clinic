package com.barmalat.medicalclinic.model.commands;

public record CreateFacilityCommand(String name, String city, String postalCode, String street, String streetNumber) {
}