package hwalibo.refactor.global.domain;

public enum Gender {
    F,M;
    public static Gender fromNaverCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        try {
            return Gender.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
