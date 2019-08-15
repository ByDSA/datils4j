package es.danisales.crypt.hash;

public enum Algorithm {
    SHA256("SHA-256"), MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA384("SHA-384"), SHA512("SHA-512");

    String val;

    Algorithm(String v) {
        val = v;
    }

    String getStringCode() {
        return val;
    }
}