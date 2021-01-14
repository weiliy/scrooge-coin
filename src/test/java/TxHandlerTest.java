import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.*;

public class TxHandlerTest {
    private static KeyPair alice;
    private static KeyPair bob;
    private static UTXOPool utxoPool;

    private static void signInput(Transaction tx, int idx, KeyPair key) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte[] txTawData = tx.getRawDataToSign(idx);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(key.getPrivate());
        sig.update(txTawData);
        byte[] realSig = sig.sign();
        tx.addSignature(realSig, idx);
    }

    private static KeyPair keyPairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        return keyGen.genKeyPair();
    }

    @BeforeClass
    static public void beforeClass() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        utxoPool = new UTXOPool();
        KeyPair scrooge = keyPairGenerator();
        alice = keyPairGenerator();
        bob = keyPairGenerator();

        Transaction sendToAlice = new Transaction();
        sendToAlice.addInput(null, 0);
        sendToAlice.addOutput(100.0, alice.getPublic());
        signInput(sendToAlice, 0, scrooge);
        sendToAlice.finalize();
        UTXO aliceUtxo = new UTXO(sendToAlice.getHash(), 0);

        utxoPool.addUTXO(aliceUtxo, sendToAlice.getOutput(0));
    }

    /*
    Test 1: test isValidTx() with valid transactions
    ==> passed
     */
    @Test
    public void when_is_valid_tx_given_valid_transactions_then_return_ture() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Transaction tx = new Transaction();
        tx.addInput(utxoPool.getAllUTXO().get(0).getTxHash(), 0);
        tx.addOutput(10., bob.getPublic());
        signInput(tx, 0, alice);
        tx.finalize();

        TxHandler txHandler = new TxHandler(utxoPool);
        Assert.assertTrue(txHandler.isValidTx(tx));
    }

    /*
    Test 2: test isValidTx() with transactions containing signatures of incorrect data
    ==> passed
     */

    /*
    Test 3: test isValidTx() with transactions containing signatures using incorrect private keys
    ==> passed

    Test 4: test isValidTx() with transactions whose total output value exceeds total input value
    ==> passed

    Test 5: test isValidTx() with transactions that claim outputs not in the current utxoPool
    ==> passed
     */

    /*
    Test 6: test isValidTx() with transactions that claim the same UTXO multiple times
    ==> passed
     */
    @Test
    public void when_is_valid_tx_given_transaction_claim_the_same_UTXO_multiple_times_then_return_false() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Transaction tx = new Transaction();

        tx.addInput(utxoPool.getAllUTXO().get(0).getTxHash(), 0);
        tx.addOutput(10., bob.getPublic());

        tx.addInput(utxoPool.getAllUTXO().get(0).getTxHash(), 0);
        tx.addOutput(10., bob.getPublic());

        signInput(tx, 0, alice);
        signInput(tx, 1, alice);

        tx.finalize();

        TxHandler txHandler = new TxHandler(utxoPool);
        Assert.assertFalse(txHandler.isValidTx(tx));
    }
    /*


    /*
    Test 7: test isValidTx() with transactions that contain a negative output value
    ==> FAILED
     */

    /*
    Test 8: test handleTransactions() with simple and valid transactions
    Total Transactions = 2
    Number of transactions returned valid by student = 2
    Total Transactions = 50
    Number of transactions returned valid by student = 50
    Total Transactions = 100
    Number of transactions returned valid by student = 100
    ==> passed
    */

    /*
    Test 9: test handleTransactions() with simple but some invalid transactions because of invalid signatures
    Total Transactions = 2
    Number of transactions returned valid by student = 0
    Total Transactions = 50
    Number of transactions returned valid by student = 2
    Total Transactions = 100
    Number of transactions returned valid by student = 1
    ==> passed
     */

    /*
    Test 10: test handleTransactions() with simple but some invalid transactions because of inputSum < outputSum
    Total Transactions = 2
    Number of transactions returned valid by student = 2
    Total Transactions = 50
    Number of transactions returned valid by student = 25
    Total Transactions = 100
    Number of transactions returned valid by student = 42
    ==> passed
     */

    /*
    Test 11: test handleTransactions() with simple and valid transactions with some double spends
    Total Transactions = 2
    Number of transactions returned valid by student = 2
    All transactions returned are not satisfied/valid
    Total Transactions = 50
    Number of transactions returned valid by student = 50
    All transactions returned are not satisfied/valid
    Total Transactions = 100
    Number of transactions returned valid by student = 100
    All transactions returned are not satisfied/valid
    ==> FAILED
     */

    /*
    Test 12: test handleTransactions() with valid but some transactions are simple, some depend on other transactions
    Total Transactions = 2
    Number of transactions returned valid by student = 2
    Total Transactions = 50
    Number of transactions returned valid by student = 50
    All transactions returned are not satisfied/valid
    Total Transactions = 100
    Number of transactions returned valid by student = 100
    All transactions returned are not satisfied/valid
    ==> FAILED
     */

    /*
    Test 13: test handleTransactions() with valid and simple but some transactions take inputs from non-exisiting utxo's
    Total Transactions = 2
    Number of transactions returned valid by student = 1
    Total Transactions = 50
    Number of transactions returned valid by student = 12
    Total Transactions = 100
    Number of transactions returned valid by student = 57
    ==> passed
     */

    /*
    Test 14: test handleTransactions() with complex Transactions
    Total Transactions = 2
    Number of transactions returned valid by student = 2
    Total Transactions = 50
    Number of transactions returned valid by student = 20
    All transactions returned are not satisfied/valid
    Total Transactions = 100
    Number of transactions returned valid by student = 63
    All transactions returned are not satisfied/valid
    ==> FAILED
     */

    /*
    Test 15: test handleTransactions() with simple, valid transactions being called again to check for changes made in the pool
    Total Transactions = 2
    Number of transactions returned valid by student = 2
    Total Transactions = 50
    Number of transactions returned valid by student = 50
    All transactions returned are not satisfied/valid
    Total Transactions = 100
    Number of transactions returned valid by student = 100
    All transactions returned are not satisfied/valid
    ==> FAILED
     */
}

