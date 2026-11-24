package util;

public class ArmazenamentoException extends RuntimeException {
    public ArmazenamentoException(String message) {
        super(message);
    }

    public ArmazenamentoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
