package vb.obama.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

import vb.obama.compiler.Import;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Utility methods for reflecting the JVM, finding classes and and converting 
 * types.
 *  
 * @version 1.2
 */
public class ReflectionUtils {
	/**
	 * Search for a given identifier. Constructs primitives and classes. The
	 * syntax for an array is ClassName+++, where the number of plus signs
	 * mean the number of nested arrays. By default, classes will be looked up
	 * under 'java.lang.*' and '*'. Additional imports can be specified
	 * 
	 * @param identifier Name of the class to load
	 * @param imports List of extra imports
	 * @return Class
	 * @throws IllegalArgumentException In case of a void array
	 * @throws ClassNotFoundException In case the class cannot be found
	 */
	public static Class<?> findType(String identifier, List<Import> imports) throws IllegalArgumentException, ClassNotFoundException {
		int dimension = identifier.indexOf('+');
		
		// Check for array type
		if (dimension > 0) {
			dimension = identifier.length() - dimension;
			identifier = identifier.substring(0, identifier.length() - dimension);
		} else {
			dimension = 0;
		}
		
		// Filter primitives
		Class<?> primitive = identifierToPrimitive(identifier);
		
		// Check for result
		if (primitive != null) {
			if (primitive.equals(void.class) && dimension > 0) {
				throw new IllegalArgumentException("Cannot create an void array");
			}
			
			// Stop here if dimension is zero
			if (dimension == 0) return primitive;
		}
		
		// Search for class, or create an array from a primitive
		List<Import> scopes = Lists.newArrayList();
		scopes.add(new Import("*"));
		scopes.add(new Import("java.lang.*"));
		scopes.addAll(imports);
		
		for (Import imp : scopes) {
			String needle = null;
			
			// Find package
			if (imp.isWildImport()) {
				needle = (imp.isBaseImport() ? "" : imp.getPackage() + ".") + identifier;
			} else if (imp.importsClass(identifier)) {
				needle = imp.getPackage();
			}
			
			if (needle != null) {
				// Build array notation if required
				if (dimension > 0) {
					needle = primitive != null ? primitiveToInternalName(primitive) : "L" + needle + ";";
					needle = Strings.repeat("[", dimension) + needle;
				}
			
				try {
					return Class.forName(needle, false, Thread.currentThread().getContextClassLoader());
				} catch (ClassNotFoundException exception) { }
			}
		}
		
		// It failed
		throw new ClassNotFoundException(String.format(
			"Cannot find class '%s%s'",
			identifier,
			Strings.repeat("[]", dimension)
		));
	}
	
	/**
	 * Convert primitive identifiers to a class
	 * 
	 * @param identifier Primitive identifier
	 * @return Class of primitive, or null.
	 */
	public static Class<?> identifierToPrimitive(String identifier) {
		Map<String, Class<?>> haystack = Maps.newHashMap();
		
		haystack.put("int", int.class);
		haystack.put("char", char.class);
		haystack.put("bool", boolean.class);
		haystack.put("void", void.class);
		
		return haystack.get(identifier);
	}
	
	/**
	 * Return the JVM identifier for a given primitive class
	 * 
	 * @param primitive Primitive
	 * @return Internal representation of class, or null
	 */
	public static String primitiveToInternalName(Class<?> primitive) {
		Map<Class<?>, String> haystack = Maps.newHashMap();
		
		haystack.put(boolean.class, "Z");
		haystack.put(byte.class, "B");
		haystack.put(char.class, "C");
		haystack.put(double.class, "D");
		haystack.put(float.class, "F");
		haystack.put(int.class, "I");
		haystack.put(long.class, "J");
		haystack.put(short.class, "S");
		haystack.put(void.class, "V");
		
		return haystack.get(primitive);
	}
	
	/**
	 * Convert a class representation to JVM representation
	 * 
	 * @param type Class
	 * @return JVM internal name
	 */
	public static String toInternalName(Class<?> type) {
		if (type.isPrimitive() || type.equals(void.class)) {
			return primitiveToInternalName(type);
		} else {
			return type.getName().replace(".", "/");
		}
	}
	
	/**
	 * Convert a list of classes to JVM representation
	 * @param types List of classes
	 * @return JVM internal representation
	 */
	public static String toInternalNames(List<Class<?>> types) {
		StringBuilder result = new StringBuilder();
		
		for (Class<?> type : types) {
			result.append(toInternalName(type));
		}
		
		return result.toString();
	}
	
	/**
	 * Create a method signature for ASM
	 * 
	 * @param returnType Method return type
	 * @param parameters List of parameters
	 * @return Method signature
	 */
	public static String toMethodDescription(Class<?> returnType, List<Class<?>> parameters) {
		return "(" + toInternalNames(parameters) + ")" + toInternalName(returnType);
	}
	
	/**
	 * Convert a list of classes to a list of types (for the ASM library)
	 * 
	 * @param types List of classes
	 * @return list of types
	 * @requires types != null
	 */
	public static Type[] toTypeArray(List<Class<?>> types) {
		checkNotNull(types);
		
		List<Type> result = Lists.transform(types, new Function<Class<?>, Type>(){
			@Override
			public Type apply(Class<?> type) {
				return Type.getType(type);
			}
		});
		
		return result.toArray(new Type[result.size()]);
	}
	
	/**
	 * Convert a list of classes to a list of strings for pretty printing
	 * 
	 * @param types List of classes
	 * @return List of strings
	 * @requires types != null
	 */
	public static List<String> prettyTypesList(List<Class<?>> types) {
		checkNotNull(types);
		
		return Lists.transform(
			types,
			new Function<Class<?>, String>() {
				@Override
				public String apply(final Class<?> input) {
					return input.getName();
				}
			}
		);
	}
}
