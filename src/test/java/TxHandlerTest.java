import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TxHandlerTest {

    @Test
    public void isValidTx() {
        UTXOPool utxoPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(utxoPool);
    }
}