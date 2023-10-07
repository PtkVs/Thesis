#define PROFILE
#include "crypto-context-bfvrns.hpp"
#include <iostream>
#include <openfhe.h>
#include "cryptocontext-ser.h"
#include "scheme/bfvrns/bfvrns-ser.h"


using namespace lbcrypto;

void cryptoContextBFVrnsGen(const char* CRYPTOFOLDER, const char* filename,
 int plaintextModulus, int ringDimension, const char* securityLevel){
  //  Set CryptoContext
    CCParams<CryptoContextBFVRNS> parameters;
    parameters.SetPlaintextModulus(plaintextModulus);
    parameters.SetRingDim(ringDimension);
     if (!strcmp(securityLevel, "SECURITY_LEVEL_128")){
        parameters.SetSecurityLevel(HEStd_128_classic);
    }
    else if (!strcmp(securityLevel, "SECURITY_LEVEL_192")){
        parameters.SetSecurityLevel(HEStd_192_classic);
    }
    else{
        parameters.SetSecurityLevel(HEStd_256_classic);
    }

    CryptoContext<DCRTPoly> cryptoContext = GenCryptoContext(parameters);
    // Enable features that you wish to use
    cryptoContext->Enable(PKE);
    cryptoContext->Enable(KEYSWITCH);
    cryptoContext->Enable(LEVELEDSHE);
    cryptoContext->Enable(PRE);
    // Serialize CryptoContext
    char path[200];
    strcpy(path, CRYPTOFOLDER);
    strcat(path,filename);
    
    if (!Serial::SerializeToFile(path, cryptoContext, SerType::BINARY)) {
        std::cerr << "Error writing serialization of Cryptocontext to : "<< path<< std::endl;
        }

}