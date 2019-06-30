package es.danisales.crypt;

import es.danisales.utils.Valuable;

@SuppressWarnings("unused")
public enum HashingAlgorithm implements Valuable<String> {
    SHA256("SHA-256"), MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA384("SHA-384"), SHA512("SHA-512");

    String val;

    HashingAlgorithm(String v) {
        val = v;
    }

    @Override
    public String getValue() {
        return val;
    }
}