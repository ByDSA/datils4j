package es.danisales.crypt;

import es.danisales.enums.EnumValue;

@SuppressWarnings("unused")
public enum HashingAlgorithm implements EnumValue<String> {
    SHA256("SHA-256"), MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA384("SHA-384"), SHA512("SHA-512");

    String val;

    HashingAlgorithm(String v) {
        val = v;
    }

    @Override
    public String value() {
        return val;
    }
}