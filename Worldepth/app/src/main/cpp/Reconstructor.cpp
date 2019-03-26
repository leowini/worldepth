#include "Reconstructor.h"

void Reconstructor::reconstruct() {
    slam = new System(vocFile, settingsFile);
    Poisson();
    TextureMap();
}