package com.byron.app.model;

public enum Idiomas {
    ES("es", "es - español"),
    EN("en", "en - inglés"),
    PT("pt", "pt - portugués"),
    FR("fr", "fr - francés"),
    IT("it", "it - italiano");

    private String idiomas;
    private String idiomaEnEspañol;

    Idiomas(String idiomas, String expresionEnEspanol) {
        this.idiomas = idiomas;
        this.idiomaEnEspañol = expresionEnEspanol;

    }

    Idiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public String getIdiomaEnEspañol() {
        return idiomaEnEspañol;
    }

    public static Idiomas fromString(String text) {
        for (Idiomas idiomasEnum : Idiomas.values()) {
            if (idiomasEnum.idiomas.equalsIgnoreCase(text)) {
                return idiomasEnum;
            }
        }
        throw new IllegalArgumentException("Ningun idioma encontrado: " + text);
    }
    public static Idiomas fromEspanol(String text) {
        for (Idiomas idiomasEnum : Idiomas.values()) {
            if (idiomasEnum.idiomaEnEspañol.equalsIgnoreCase(text)) {
                return idiomasEnum;
            }
        }
        throw new IllegalArgumentException("Ninguna categoria encontrada: " + text);
    }
}
