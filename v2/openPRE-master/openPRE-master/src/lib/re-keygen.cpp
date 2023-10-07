#define PROFILE  // for TIC TOC

#include "re-keygen.hpp"
#include "openfhe.h"
#include "cryptocontext-ser.h"
#include "key/key-ser.h"

using namespace lbcrypto;

using CT = Ciphertext<DCRTPoly>;  // ciphertext
using PT = Plaintext;

void reKeyGen(const char * secretKey, const char* publicKey, const char* destinationPath){
    //  Deserialize the private key
    PrivateKey<DCRTPoly> sk;
    if (!Serial::DeserializeFromFile(secretKey, sk, SerType::BINARY)) {
        std::cerr << "I cannot read serialization of private key from : "<< secretKey << std::endl;
        }
    // Get the crypto Context from secret key
    CryptoContext<DCRTPoly> cryptoContext;
    cryptoContext = sk.get()->GetCryptoContext();
    // Deserialize the public key
    PublicKey<DCRTPoly> pk;
    if (!Serial::DeserializeFromFile(publicKey, pk, SerType::BINARY)) {
        std::cerr << "I cannot read serialization of Public Key from : "<< publicKey << std::endl;
        }

    EvalKey<DCRTPoly> reEncryptionKey;
    // Generate Re Encryption Key
    reEncryptionKey = cryptoContext->ReKeyGen(sk, pk);
    // Serialize the re-encryption key
    char path[200];
    strcpy(path, destinationPath);
    strcat(path,"-re-enc-key");
    if (!Serial::SerializeToFile(path, reEncryptionKey, SerType::BINARY)) {
        std::cerr << "Error writing serialization of re-encryption key to :  "<< path<< std::endl;
        }
}