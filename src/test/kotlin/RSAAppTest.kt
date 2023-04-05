import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import javax.xml.bind.DatatypeConverter

class RSATests {
    companion object {
        @JvmStatic
        fun inputTextData(): List<Arguments> {
            return listOf(
                Arguments.of("Hello World!"),
                Arguments.of(""),
                Arguments.of("qwertyuiop[]asdfghjkl;'zxcvbnm,./1234567890-"),
                Arguments.of("Just normal text")
            )
        }
    }

    @Test
    fun `encrypt and decrypt a string`() {
        val keyPair = generateRSAKeys()
        val publicKey = keyPair.public
        val privateKey = keyPair.private

        val plainText = "Hello world!"
        val encrypted = encryptRSA(plainText, publicKey)
        val decrypted = decryptRSA(encrypted, privateKey)

        assertEquals(plainText, decrypted)
    }

    @Test
    fun `create and read public and private keys from bytes`() {
        val keyPair = generateRSAKeys()
        val publicKeyBytes = keyPair.public.encoded
        val privateKeyBytes = keyPair.private.encoded

        val publicKey = createPublicKey(publicKeyBytes)
        val privateKey = createPrivateKey(privateKeyBytes)

        assertEquals(publicKey, keyPair.public)
        assertEquals(privateKey, keyPair.private)
    }

    @ParameterizedTest
    @MethodSource("inputTextData")
    fun `encrypt and decrypt a file`(rawText: String) {
        val keyPair = generateRSAKeys()
        val publicKeyBytes = keyPair.public.encoded
        val privateKeyBytes = keyPair.private.encoded

        val publicKeyString = DatatypeConverter.printHexBinary(publicKeyBytes)
        val privateKeyString = DatatypeConverter.printHexBinary(privateKeyBytes)

        val inputFileName = "test_input.txt"
        val outputFileName = "test_output.txt"

        writeToFile(inputFileName, rawText)

        val encryptArgs = arrayOf(
            "encrypt",
            "-i", inputFileName,
            "-o", outputFileName,
            "-k", publicKeyString,
        )

        main(encryptArgs)

        // Decrypt
        val decryptArgs = arrayOf(
            "decrypt",
            "-i", outputFileName,
            "-o", inputFileName,
            "-k", privateKeyString,
        )
        main(decryptArgs)

        val decryptedText = readFile(inputFileName)

        assertEquals(rawText, decryptedText)

        File(inputFileName).delete()
        File(outputFileName).delete()
    }
}