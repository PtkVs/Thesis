#define PROFILE

#include <string>
#include <iostream>
#include <sstream>

#include "ciphertext-ser.h"
#include "cryptocontext-ser.h"
#include "key/key-ser.h"
#include "openfhe.h"
#include "decryption.hpp"


using namespace lbcrypto;

using CT = Ciphertext<DCRTPoly>;  // ciphertext
using PT = Plaintext;             // plaintext

using vecInt  = std::vector<int64_t>;  // vector of ints
using vecChar = std::vector<char>;     // vector of characters

char* decrypt(const char* secretKey, const char* ciphertext){
    // Deserialize the private key
    PrivateKey<DCRTPoly> sk;
    if (!Serial::DeserializeFromFile(secretKey, sk, SerType::BINARY)) {
        std::cerr << "I cannot read serialization of private key from : "<< secretKey << std::endl;
        }
    // Get the Crypto Context from secret key
    CryptoContext<DCRTPoly> cryptoContext;
    cryptoContext = sk.get()->GetCryptoContext();
    CT ct;
    PT pt;

    //  Deserialize the ciphertext
    if (!Serial::DeserializeFromFile(ciphertext, ct, SerType::BINARY)) {
        std::cerr << "I cannot read serialization of Ciphertext from : "<< ciphertext << std::endl;
        }
    //  Decryption
    cryptoContext->Decrypt(sk, ct, &pt);
    std::string plaintext = pt->GetStringValue();
    return strcpy(new char[plaintext.length() + 1], plaintext.c_str());
    

}

