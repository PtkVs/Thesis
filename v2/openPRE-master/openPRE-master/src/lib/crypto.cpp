#include "crypto.hpp"
#include <iostream>
#include <openfhe.h>
#include "crypto-context-bfvrns.hpp"
#include "crypto-context-bgvrns.hpp"


using namespace lbcrypto;


void cryptoContextGen(const char* schemeName,
 const char* CRYPTOFOLDER,
  const char* filename,
   int plaintextModulus,
    int ringDimension,
     const char* securityLevel){
    if (!strcmp(schemeName, "BGV")){
        cryptoContextBGVrnsGen(CRYPTOFOLDER, filename, plaintextModulus, ringDimension, securityLevel);
    }
    else if (!strcmp(schemeName, "BFV")){
        cryptoContextBFVrnsGen(CRYPTOFOLDER, filename, plaintextModulus, ringDimension, securityLevel);
    }
    else{
        std::cerr << "Scheme Error: Undefined scheme name"
                  << std::endl;
    }
}
