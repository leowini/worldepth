//
// Created by leodw on 1/21/2019.
//

#include <time.h>

#include "ORBVocabulary.h"
using namespace std;

bool load_as_text(SLAM::ORBVocabulary* voc, const std::string infile) {
    clock_t tStart = clock();
    bool res = voc->loadFromTextFile(infile);
    printf("Loading fom text: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
    return res;
}

void load_as_xml(SLAM::ORBVocabulary* voc, const std::string infile) {
    clock_t tStart = clock();
    voc->load(infile);
    printf("Loading fom xml: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
}

void load_as_binary(SLAM::ORBVocabulary* voc, const std::string infile) {
    clock_t tStart = clock();
    voc->loadFromBinaryFile(infile);
    printf("Loading fom binary: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
}

void save_as_xml(SLAM::ORBVocabulary* voc, const std::string outfile) {
    clock_t tStart = clock();
    voc->save(outfile);
    printf("Saving as xml: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
}

void save_as_text(SLAM::ORBVocabulary* voc, const std::string outfile) {
    clock_t tStart = clock();
    voc->saveToTextFile(outfile);
    printf("Saving as text: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
}

void save_as_binary(SLAM::ORBVocabulary* voc, const std::string outfile) {
    clock_t tStart = clock();
    voc->saveToBinaryFile(outfile);
    printf("Saving as binary: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
}


int main(int argc, char **argv) {
    cout << "BoW load/save benchmark" << endl;
    SLAM::ORBVocabulary* voc = new SLAM::ORBVocabulary();

    load_as_text(voc, "Vocabulary/ORBvoc.txt");
    save_as_binary(voc, "Vocabulary/ORBvoc.bin");

    return 0;
}