import kotlinx.cli.*
import java.io.File
import java.io.FileWriter
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import javax.xml.bind.DatatypeConverter
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

enum class Commands {
    ENCRYPT,
    DECRYPT,
    GENERATE_KEYS
}

fun encryptRSA(plainText: String, publicKey: PublicKey): ByteArray {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    return cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
}

fun decryptRSA(encrypted: ByteArray, privateKey: PrivateKey): String {
    val cipher = Cipher.getInstance("RSA")
    cipher.init(Cipher.DECRYPT_MODE, privateKey)
    val decrypted = cipher.doFinal(encrypted)
    return String(decrypted, Charsets.UTF_8)
}

fun createPublicKey(publicKeyBytes: ByteArray): PublicKey? {
    val keyFactory = KeyFactory.getInstance("RSA")
    val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
    return keyFactory.generatePublic(publicKeySpec)
}

fun createPrivateKey(privateKeyBytes: ByteArray): PrivateKey? {
    val keyFactory = KeyFactory.getInstance("RSA")
    val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
    return keyFactory.generatePrivate(privateKeySpec)
}

fun generateRSAKeys(keySize: Int = 2048): KeyPair {
    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(keySize)
    return keyPairGenerator.genKeyPair()
}

fun readFile(fileName: String) = File(fileName).readText()
fun writeToFile(fileName: String, text: String) = FileWriter(fileName).use { writer -> writer.write(text) }

fun main(args: Array<String>) {
    val parser = ArgParser("RSA-slawa4s")
    val inputFile by parser.option(ArgType.String, shortName = "i", description = "Input file")
    val outputFile by parser.option(ArgType.String, shortName = "o", description = "Output file")
    val providedKey by parser.option(ArgType.String, shortName = "k", description = "Public or private key")
    val command by parser.argument(ArgType.Choice<Commands>(), description = "Command")
    parser.parse(args)

    when (command) {
        Commands.GENERATE_KEYS -> {
            val key = generateRSAKeys()
            println("Generated keys:\n" +
                    "PUBLIC KEY (saved to public_key.pem): ${ DatatypeConverter.printHexBinary(key.public.encoded) } \n" +
                    "PRIVATE KEY (saved to private_key.pem): ${ DatatypeConverter.printHexBinary(key.private.encoded) }\n" )
            writeToFile("public_key.pem", DatatypeConverter.printHexBinary(key.public.encoded))
            writeToFile("private_key.pem", DatatypeConverter.printHexBinary(key.private.encoded))
        }
        Commands.ENCRYPT -> {
            if (inputFile == null || outputFile == null || providedKey == null) {
                throw IllegalArgumentException("Both input and output files and public key are required for encryption!")
            }
            val publicKey = createPublicKey(DatatypeConverter.parseHexBinary(providedKey!!))
                ?: throw IllegalArgumentException("Invalid public key!")
            val text = readFile(inputFile!!)
            val encryptedBinary = DatatypeConverter.printHexBinary(encryptRSA(text, publicKey))
            writeToFile(outputFile!!, encryptedBinary)
        }
        Commands.DECRYPT -> {
            if (inputFile == null || outputFile == null || providedKey == null) {
                throw IllegalArgumentException("Both input and output files and private key are required for decryption!")
            }
            val privateKey = createPrivateKey(DatatypeConverter.parseHexBinary(providedKey!!))
                ?: throw IllegalArgumentException("Invalid private key!")
            val encrypted = readFile(inputFile!!)
            val decryptedText = decryptRSA(DatatypeConverter.parseHexBinary(encrypted), privateKey)
            writeToFile(outputFile!!, decryptedText)
        }
    }
}