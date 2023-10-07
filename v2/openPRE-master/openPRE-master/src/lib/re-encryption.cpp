#define PROFILE
#include <string>
#include "ciphertext-ser.h"
#include "cryptocontext-ser.h"
#include "key/key-ser.h"
#include "openfhe.h"
#include "re-encryption.hpp"

using namespace lbcrypto;

using CT = Ciphertext<DCRTPoly>;  // ciphertext
using PT = Plaintext;             // plaintext

using vecInt  = std::vector<int64_t>;  // vector of ints
using vecChar = std::vector<char>;     // vector of characters

void reEncrypt(const char* ciphertext, const char* reEncryptionKey, const char* destinationPath){
    //  Deserialize the re-encryption key
    EvalKey<DCRTPoly> rk;
    if (!Serial::DeserializeFromFile(reEncryptionKey, rk, SerType::BINARY)) {
        std::cerr << "I cannot read serialization from : "<< reEncryptionKey << std::endl;
        }
    // Get the crypto context from re encryption key
    CryptoContext<DCRTPoly> cryptoContext;
    cryptoContext = rk.get()->GetCryptoContext();
    //  Deserialize the Ciphertext
    CT ct;
    if (!Serial::DeserializeFromFile(ciphertext, ct, SerType::BINARY)) {
        std::cerr << "I cannot read serialization of Ciphertext from : "<< ciphertext << std::endl;
        }
    //  Re encrypt the ciphertext
    auto ct2 = cryptoContext->ReEncrypt(ct, rk);
    // Serialize ciphertext re encrypted
    if (!Serial::SerializeToFile(destinationPath, ct2, SerType::BINARY)) {
        std::cerr << "Error writing serialization of ciphertext from : "<< destinationPath<< std::endl;
        }

}
