#ifndef EASYPR_ACCURACY_HPP
#define EASYPR_ACCURACY_HPP

#include <easypr.h>
#include <ctime>
#include <fstream>
#include <list>
#include <memory>
#include <numeric>
#include "easypr/core/core_func.h"
#include "easypr/util/util.h"
#include "thirdparty/xmlParser/xmlParser.h"
#include "easypr/core/params.h"
#include "config.hpp"
#include "result.hpp"

using namespace std;

namespace easypr {

  namespace demo {

    int getGroundTruth(map<string, vector<CPlate>>& xmlMap, const char* path) {

#ifdef OS_WINDOWS
      XMLNode::setGlobalOptions(XMLNode::char_encoding_GBK);
#endif
      XMLNode xMainNode = XMLNode::openFileHelper(path, "tagset");

      int n = xMainNode.nChildNode("image");

      // this prints the "coefficient" value for all the "NumericPredictor" tags:
      for (int i = 0; i < n; i++) {
        XMLNode imageNode = xMainNode.getChildNode("image", i);
        string imageName = imageNode.getChildNode("imageName").getText();

        vector<CPlate> plateVec;
        int m = imageNode.getChildNode("taggedRectangles").nChildNode("taggedRectangle");
        for (int j = 0; j < m; j++) {
          XMLNode plateNode = imageNode.getChildNode("taggedRectangles").getChildNode("taggedRectangle", j);

          int x = atoi(plateNode.getAttribute("x"));
          int y = atoi(plateNode.getAttribute("y"));
          int width = atoi(plateNode.getAttribute("width"));
          int height = atoi(plateNode.getAttribute("height"));
          int angle = atoi(plateNode.getAttribute("rotation"));

          string plateStr = plateNode.getText();

          if (width < height) {
            std::swap(width, height);
            angle = angle + 90;
          }

          RotatedRect rr(Point2f(float(x), float(y)), Size2f(float(width), float(height)), (float)angle);

          CPlate plate;
          plate.setPlateStr(plateStr);
          plate.setPlatePos(rr);
          plateVec.push_back(plate);
        }
        xmlMap[imageName] = plateVec;
      }
      return 0;
    }


    int gridSearchTest(const char* test_path) {

      std::vector<Result> all_results;

      int i1[] = { 900, 1000, 1100, 1200 };
      int i1_c = 4;

      float f1[] = { 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.35f, 0.4f, 0.45f, 0.5f, 0.55f, 0.6f, 0.65f, 0.7f };
      int f1_c = 13;

      float f2[] = { 0.2f, 0.25f, 0.3f, 0.35f };
      int f2_c = 4;

      float f3[] = { 0.4f, 0.45f, 0.5f, 0.55f };
      int f3_c = 4;

      Config config;
      config.setParam1f(f1, f1_c);
      for (size_t idx1 = 0; idx1 < config.getParam1f().size(); idx1++) {
        float f1 = config.getParam1f().at(idx1);
        CParams::instance()->setParam1f(f1);
        Result result;
         result.getParams().setParam1f(f1);
        all_results.push_back(result);
      }

      /*Config config;
      config.setParam1i(i1, i1_c);
      config.setParam1f(f1, f1_c);
      config.setParam2f(f2, f2_c);
      config.setParam3f(f3, f3_c);

      for (size_t idx1 = 0; idx1 < config.getParam1f().size(); idx1++) {
        float f1 = config.getParam1f().at(idx1);

        for (size_t idx2 = 0; idx2 < config.getParam2f().size(); idx2++) {
          float f2 = config.getParam2f().at(idx2);

          for (size_t idx3 = 0; idx3 < config.getParam3f().size(); idx3++) {
            float f3 = config.getParam3f().at(idx3);

            for (size_t idx4 = 0; idx4 < config.getParam1i().size(); idx4++) {
              int i1 = config.getParam1i().at(idx4);

              CParams::instance()->setParam1i(i1);
              CParams::instance()->setParam1f(f1);
              CParams::instance()->setParam2f(f2);
              CParams::instance()->setParam3f(f3);

              Result result;
              accuracyTest(test_path, result, true);

              result.getParams().setParam1i(i1);
              result.getParams().setParam1f(f1);
              result.getParams().setParam2f(f2);
              result.getParams().setParam3f(f3);

              all_results.push_back(result);
            }
          }        
        }
      }*/

      std::sort(all_results.begin(), all_results.end(),
        [](const Result& r1, const Result& r2) {
        return r1.getDetectFscore() > r2.getDetectFscore();
      });

      for (auto result : all_results) {
        std::cout << result << std::endl;
 
        ofstream myfile("result/gridSearch.txt", ios::app);
        if (myfile.is_open()) {
          time_t t = time(0);  // get time now
          struct tm* now = localtime(&t);
          char buf[80];

          strftime(buf, sizeof(buf), "%Y-%m-%d %X", now);
          myfile << string(buf) << endl;
          myfile << result << std::endl;
          myfile.close();
        }
      }
      
      return 0;
    }



  }



}

#endif  // EASYPR_ACCURACY_HPP
