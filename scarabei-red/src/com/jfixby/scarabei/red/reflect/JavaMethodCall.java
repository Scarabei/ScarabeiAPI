
package com.jfixby.scarabei.red.reflect;

import java.lang.reflect.Method;

import com.jfixby.scarabei.api.codecs.calls.io.CrossLanguageMethodCall;
import com.jfixby.scarabei.api.codecs.calls.io.CrossLanguageMethodCallArgument;
import com.jfixby.scarabei.api.names.ID;
import com.jfixby.scarabei.api.reflect.Reflections;

public class JavaMethodCall {
	public ID methodName;
	public String[] argumentNames;
	public Class<?>[] argumentTypes;
	public Object[] argumentValues;
	private Method method;

	public Object invoke () throws Exception {
		Object result = null;
		if (this.argumentValues.length == 0) {
			result = this.method.invoke(null);
		} else {
			result = this.method.invoke(null, this.argumentValues);
		}
		return result;
	}

	public static JavaMethodCall fromCrossLanguageMethodCall (final CrossLanguageMethodCall crossCall)
		throws ReflectiveOperationException {
		final JavaMethodCall javaCall = new JavaMethodCall();

		javaCall.methodName = crossCall.methodName;

		javaCall.argumentNames = new String[crossCall.arguments.size()];
		javaCall.argumentValues = new Object[crossCall.arguments.size()];
		javaCall.argumentTypes = new Class[crossCall.arguments.size()];

		int i = 0;
		for (final CrossLanguageMethodCallArgument argument : crossCall.arguments) {
			javaCall.argumentNames[i] = argument.argumentName;
			javaCall.argumentValues[i] = argument.argumentValue;
			if (javaCall.argumentValues[i] == null) {
				throw new Error("Argument <" + javaCall.argumentNames[i] + "> is null.");
			}
			javaCall.argumentTypes[i] = argument.argumentType;
			i++;
		}

		{
			final ID methodFullName = crossCall.methodName;

			final String methodName = methodFullName.getLastStep();
			final ID className = methodFullName.parent();
			final Class<?> klass = Reflections.resolveClass(className);
// L.d("javaCall.class", klass);
// L.d("javaCall.method", methodName);
// L.d("javaCall.arguments", javaCall.);

			final Class<?>[] argTypes = javaCall.argumentTypes;
			final Object[] argValues = javaCall.argumentValues;
// L.d("argTypes", Collections.newList(argTypes));
// L.d("argValues", Collections.newList(argValues));

			if (argTypes.length == 0) {
				javaCall.method = klass.getMethod(methodName);
			} else {
				javaCall.method = klass.getMethod(methodName, argTypes);
			}

		}

		return javaCall;
	}

}
