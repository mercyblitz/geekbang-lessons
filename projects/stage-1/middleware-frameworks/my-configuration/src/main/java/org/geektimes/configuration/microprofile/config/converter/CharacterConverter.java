package org.geektimes.configuration.microprofile.config.converter;

public class CharacterConverter extends AbstractConverter<Character> {

    @Override
    protected Character doConvert(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Character.valueOf(value.charAt(0));
    }
}
