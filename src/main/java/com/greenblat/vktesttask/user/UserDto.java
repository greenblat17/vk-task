package com.greenblat.vktesttask.user;

public record UserDto(
        Long id,
        String name,
        String username,
        String email,
        AddressDto address,
        String phone,
        String website,
        CompanyDto company
) {

    public record AddressDto(
            String street,
            String suite,
            String city,
            String zipcode,
            GeoDto geo) {
    }

    public record GeoDto(
            String lat,
            String lng) {
    }

    public record CompanyDto(
            String name,
            String catchPhrase,
            String bs) {
    }

}
