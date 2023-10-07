#define PROFILE
#include "keygen.hpp"
#include <iostream>
#include <openfhe.h>
#include "cryptocontext-ser.h"
#include "key/key-ser.h"

using namespace lbcrypto;


void keysGen(const char* cryptoContextPath, const char* destinationPath){
    // Deserialize the crypto context
    CryptoContext<DCRTPoly> cryptoContext;
    if (!Serial::DeserializeFromFile(cryptoContextPath, cryptoContext, SerType::BINARY)) {
            std::cerr << "I cannot read serialization from "<< cryptoContextPath << std::endl;
        }
    // Initialize Key Pair Containers
    KeyPair<DCRTPoly> keyPair;
    // Generate a public/private key pair 
    keyPair = cryptoContext->KeyGen();
    // Serialize the public key
    char path[200];
    strcpy(path, destinationPath);
    strcat(path,"-public-key");
    if (!Serial::SerializeToFile(path, keyPair.publicKey, SerType::BINARY)) {
        std::cerr << "Error writing serialization of public key to :  "<< path<< std::endl;
        }
    // Serialize the secret key
    strcpy(path, destinationPath);
    strcat(path,"-private-key");
    if (!Serial::SerializeToFile(path, keyPair.secretKey, SerType::BINARY)) {
        std::cerr << "Error writing serialization of private key to :  "<< path<< std::endl;
        }

}