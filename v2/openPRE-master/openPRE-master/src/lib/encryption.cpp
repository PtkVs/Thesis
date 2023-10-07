#define PROFILE
#include <string>
#include "ciphertext-ser.h"
#include "cryptocontext-ser.h"
#include "key/key-ser.h"
#include "openfhe.h"
#include "encryption.hpp"

using namespace lbcrypto;

using CT = Ciphertext<DCRTPoly>;  // ciphertext
using PT = Plaintext;             // plaintext

using vecInt  = std::vector<int64_t>;  // vector of ints
using vecChar = std::vector<char>;     // vector of characters

void encrypt(const char* publickey, const char* plaintext, const char* destinationPath){
    //  Deserialize the publickey
    PublicKey<DCRTPoly> pk;
    if (!Serial::DeserializeFromFile(publickey, pk, SerType::BINARY)) {
        std::cerr << "I cannot read serialization of Public key from : "<< publickey << std::endl;
        }
    // Get the crypto context from pk
    CryptoContext<DCRTPoly> cryptoContext;
    cryptoContext = pk.get()->GetCryptoContext();
    //  Create a plaintext object from string input
    std::string strplaintext = plaintext;
    Plaintext pt = cryptoContext->MakeStringPlaintext(strplaintext);    
    //  Encryption
    auto ct = cryptoContext->Encrypt(pk, pt);
    // Serialize ciphertext in BINARY
    if (!Serial::SerializeToFile(destinationPath, ct, SerType::BINARY)) {
        std::cerr << "Error writing serialization of ciphertext from : "<< destinationPath<< std::endl;
        }

}
