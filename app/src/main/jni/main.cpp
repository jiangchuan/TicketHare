#include "easypr.h"
#include "easypr/util/switch.hpp"

#include "accuracy.hpp"
#include "chars.hpp"
#include "plate.hpp"

using namespace easypr;
int main(int argc, const char* argv[]) {
  std::shared_ptr<easypr::Kv> kv(new easypr::Kv);
  kv->load("etc/chinese_mapping");
  CPlateRecognize pr;
  pr.setResultShow(false);
  pr.setDetectType(PR_DETECT_CMSER);

  vector<CPlate> plateVec;
  Mat src = imread("resources/image/test.jpg");
  imshow("img", src);
  int result = pr.plateRecognize(src, plateVec);
  for (int i = 0; i < plateVec.size(); i++)
  {
	  CPlate plate = plateVec.at(i);
	  Mat plateMat = plate.getPlateMat();
	  RotatedRect rrect = plate.getPlatePos();
	  string license = plate.getPlateStr();
	  cout << license << endl;
  }
  waitKey();
  return 0;
}