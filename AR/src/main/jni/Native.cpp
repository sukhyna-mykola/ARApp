
#include <AR/gsub_es.h>
#include <Eden/glm.h>
#include <jni.h>
#include <ARWrapper/ARToolKitWrapperExportedAPI.h>
#include <unistd.h> // chdir()
#include <android/log.h>
#include <vector>
#include <iterator>
#include <mutex>

using namespace std;

// Utility preprocessor directive so only one change needed if Java class name changes
#define JNIFUNCTION_DEMO(sig) Java_com_mykola_ar_Native_##sig

extern "C" {

	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeShutdown(JNIEnv* env, jobject object));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeSurfaceCreated(JNIEnv* env, jobject object));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeSurfaceChanged(JNIEnv* env, jobject object, jint w, jint h));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeDrawFrame(JNIEnv* env, jobject obj));
	JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(ativeGetObjsNumber(JNIEnv* env, jobject object));
	JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(nativeAddObj(JNIEnv* env, jobject object, jstring path, jstring pattern, jfloat scale));

    JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeRotateModel(JNIEnv* env, jobject object, jint position, jfloat andle, jfloat x,jfloat y,jfloat z)) ;
    JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeTranslateModel(JNIEnv* env, jobject object, jint position, jfloat x,jfloat y,jfloat z));
    JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeScaleModel(JNIEnv* env, jobject object, jint position, jfloat s));

    };

typedef struct ARModel {
	int patternID;
	ARdouble transformationMatrix[16];
	bool visible;
	GLMmodel* obj;

} ARModel;

#define NUM_MODELS 0
//static ARModel models[NUM_MODELS] = {0};
static vector<ARModel> models(NUM_MODELS);
static mutex m;

static float lightAmbient[4] = {0.1f, 0.1f, 0.1f, 1.0f};
static float lightDiffuse[4] = {1.0f, 1.0f, 1.0f, 1.0f};
static float lightPosition[4] = {0.0f, 0.0f, 1.0f, 0.0f};



JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeShutdown(JNIEnv* env, jobject object)) {
}


JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeSurfaceCreated(JNIEnv* env, jobject object)) {
	glStateCacheFlush(); // Make sure we don't hold outdated OpenGL state.
	//for (int i = 0; i < NUM_MODELS; i++) {
	   for (int i = 0; i < models.size(); i++) {
	     if (models[i].obj) {
	        glmDelete(models[i].obj, 0);
	        models[i].obj = NULL;
	    }
	}
}

JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeSurfaceChanged(JNIEnv* env, jobject object, jint w, jint h)) {
	// glViewport(0, 0, w, h) has already been set.
}

JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeDrawFrame(JNIEnv* env, jobject obj)) {
    m.lock();
	glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Set the projection matrix to that provided by ARToolKit.
	float proj[16];
	arwGetProjectionMatrix(proj);
	glMatrixMode(GL_PROJECTION);
	glLoadMatrixf(proj);

	glMatrixMode(GL_MODELVIEW);

	glStateCacheEnableDepthTest();
	glStateCacheEnableLighting();
	glEnable(GL_LIGHT0);

	//for (int i = 0; i < NUM_MODELS; i++) {
	for (int i = 0; i < models.size(); i++) {

		models[i].visible = arwQueryMarkerTransformation(models[i].patternID, models[i].transformationMatrix);

		if (models[i].visible) {
			glLoadMatrixf(models[i].transformationMatrix);


			glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
			glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
			glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);

			glmDrawArrays(models[i].obj, 0);
		}

	}
        m.unlock();

    }
// My own functions
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeScaleModel(JNIEnv* env, jobject object, jint position, jfloat s)) {

     m.lock();
       if(position>=0 && position<models.size()){
                    if(models[position].obj){

                        glmScale(models[position].obj, s);
                        glmDeleteArrays(models[position].obj);
                   		glmCreateArrays(models[position].obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);
                   		}
                    }

        m.unlock();
    }

         /*
         * Метод переміщує 3D модель в задану координату
         * параметри:
         * position - позиція(індекс) 3d моделі в списку
         * x,y,z - координата куди помісти 3d обєкт
         */

    	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeTranslateModel(JNIEnv* env, jobject object, jint position, jfloat x,jfloat y,jfloat z)) {
           GLfloat point [] = {x,y,z};

            if(models[position].obj){
                glmTranslate(models[position].obj, point);
                glmDeleteArrays(models[position].obj);
           		glmCreateArrays(models[position].obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);
           		}


        }

         JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeRotateModel(JNIEnv* env, jobject object, jint position, jfloat angle, jfloat x,jfloat y,jfloat z)) {
             if(models[position].obj){

                        glmRotate(models[position].obj,angle,x,y,z);
                        glmDeleteArrays(models[position].obj);
                   		glmCreateArrays(models[position].obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);
                   		}


                }


        /*
        *Метод додає новий елемент в Vector
        *
        *params:
        *path - шлях де знаходиться .obj файл
        *pattern - шлях де знаходиться файл маркера (patt файл)
        *scale - маштабуючий параметер
        *
        *return: id нового маркера
        */
       JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(nativeAddObj(JNIEnv* env, jobject object, jstring path, jstring pattern, jfloat scale)){
         ARModel model;

         model.visible = false;

         const char *id = env->GetStringUTFChars(pattern , NULL) ;
         model.patternID = arwAddMarker(id);

         if(model.patternID!=-1){

            arwSetMarkerOptionBool(model.patternID, ARW_MARKER_OPTION_SQUARE_USE_CONT_POSE_ESTIMATION, false);
            arwSetMarkerOptionBool(model.patternID, ARW_MARKER_OPTION_FILTERED, true);

             const char *data = env->GetStringUTFChars(path , NULL ) ;
             model.obj = glmReadOBJ3(data, 0, 0,true);

             if (!model.obj) {
            	LOGE("Error loading model from file '%s'.", data);
            	return -1;
              }
              glmScale(model.obj, scale);
            	//glmRotate(models[2].obj, 3.14159f / 2.0f, 1.0f, 0.0f, 3.0f);
              glmCreateArrays(model.obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);

              models.push_back(model);
         }

         return  model.patternID;

       }

        JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(nativeGetObjsNumber(JNIEnv* env, jobject object)){
        return models.size();



       }
