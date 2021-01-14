import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TxHandler {

    private UTXOPool utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        ArrayList<Transaction.Output> outputs = tx.getOutputs();
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        Set<UTXO> utxos = new HashSet<>();

        for (int idx = 0; idx < inputs.size(); idx++) {
            Transaction.Input input = inputs.get(idx);

            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (utxos.contains(utxo)) {
                return false;
            } else {
                utxos.add(utxo);
            }

            Transaction.Output prevOutput = utxoPool.getTxOutput(utxo);

            PublicKey publicKey;
            try {
                publicKey = prevOutput.address;
            } catch(NullPointerException e) {
                return false;
            }

            byte[] rawDataToSign = tx.getRawDataToSign(idx);
            if (!Crypto.verifySignature(publicKey, rawDataToSign, input.signature)) {
                return false;
            }
        }

        Double outputsSum = outputs.stream().map(output -> output.value).reduce(0.0, Double::sum);
        Double inputsSum = inputs.stream().map(input -> utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value).reduce(0.0, Double::sum);

        return inputsSum >= outputsSum;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        List<Transaction> validTxs = new ArrayList<>();
        for (Transaction tx : possibleTxs) {
            if (isValidTx(tx)) {
                validTxs.add(tx);
                ArrayList<Transaction.Output> outputs = tx.getOutputs();
                for (int idx = 0; idx < outputs.size(); idx++) {
                    UTXO utxo = new UTXO(tx.getHash(), idx);
                    utxoPool.addUTXO(utxo, outputs.get(idx));
                }
            }
        }

        return validTxs.toArray(new Transaction[0]);
    }

}
