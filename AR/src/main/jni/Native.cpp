/*
 *  ARWrapperNativeCarsExample.cpp
 *  ARToolKit for Android
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri LLC. All Rights Reserved.
 *  Copyright 2011-2015 ARToolworks, Inc. All Rights Reserved.
 *
 *  Author(s): Julian Looser, Philip Lamb
 */

#include <AR/gsub_es.h>
#include <Eden/glm.h>
#include <jni.h>
#include <ARWrapper/ARToolKitWrapperExportedAPI.h>
#include <unistd.h> // chdir()
#include <android/log.h>
#include <vector>
#include <iterator>
using namespace std;

// Utility preprocessor directive so only one change needed if Java class name changes
#define JNIFUNCTION_DEMO(sig) Java_com_mykola_ar_Native_##sig

extern "C" {

	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeShutdown(JNIEnv* env, jobject object));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeSurfaceCreated(JNIEnv* env, jobject object));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeSurfaceChanged(JNIEnv* env, jobject object, jint w, jint h));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(nativeDrawFrame(JNIEnv* env, jobject obj));
	JNIEXPORT jfloat JNICALL JNIFUNCTION_DEMO(scale(JNIEnv* env, jobject object, jfloat s));
	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(translate(JNIEnv* env, jobject object, jfloat x,jfloat y,jfloat z));
	JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(ativeGetObjsNumber(JNIEnv* env, jobject object));
	JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(nativeAddObj(JNIEnv* env, jobject object, jstring path, jstring pattern, jfloat scale));
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


    }
// My own functions
	JNIEXPORT jfloat JNICALL JNIFUNCTION_DEMO(scale(JNIEnv* env, jobject object, jfloat s)) {
     //
       for (int i = 0; i < models.size(); i++) {
       //	for (int i = 0; i < NUM_MODELS; i++) {
        if(models[i].obj){
            glmScale(models[i].obj, s);
            glmDeleteArrays(models[i].obj);
       		glmCreateArrays(models[i].obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);
       		}

       	}


        return s;

    }

    	JNIEXPORT void JNICALL JNIFUNCTION_DEMO(translate(JNIEnv* env, jobject object, jfloat x,jfloat y,jfloat z)) {
           GLfloat point [] = {x,y,z};
           for (int i = 0; i < models.size(); i++) {
         //	for (int i = 0; i < NUM_MODELS; i++) {
            if(models[i].obj){
                glmTranslate(models[i].obj, point);
                glmDeleteArrays(models[i].obj);
           		glmCreateArrays(models[i].obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);
           		}

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

         arwSetMarkerOptionBool(model.patternID, ARW_MARKER_OPTION_SQUARE_USE_CONT_POSE_ESTIMATION, false);
         arwSetMarkerOptionBool(model.patternID, ARW_MARKER_OPTION_FILTERED, true);

         const char *data = env->GetStringUTFChars(path , NULL ) ;
         model.obj = glmReadOBJ3(data, 0, 0,true);

            if (!model.obj) {
            	LOGE("Error loading model from file '%s'.", data);
            	exit(-1);
            }
         glmScale(model.obj, scale);
            	//glmRotate(models[2].obj, 3.14159f / 2.0f, 1.0f, 0.0f, 3.0f);
         glmCreateArrays(model.obj, GLM_SMOOTH | GLM_MATERIAL | GLM_TEXTURE);

         models.push_back(model);

         return  model.patternID;

       }

        JNIEXPORT jint JNICALL JNIFUNCTION_DEMO(nativeGetObjsNumber(JNIEnv* env, jobject object)){
        return models.size();
       }

