
package com.jfixby.scarabei.red.geometry;

import com.jfixby.scarabei.api.collections.Collection;
import com.jfixby.scarabei.api.collections.CollectionConverter;
import com.jfixby.scarabei.api.collections.Collections;
import com.jfixby.scarabei.api.collections.EditableCollection;
import com.jfixby.scarabei.api.collections.List;
import com.jfixby.scarabei.api.floatn.Float2;
import com.jfixby.scarabei.api.floatn.ReadOnlyFloat2;
import com.jfixby.scarabei.api.geometry.Spline2D;

public class RedSpline2D implements Spline2D {
	final CollectionConverter<ReadOnlyFloat2, Vec3D> converter = new CollectionConverter<ReadOnlyFloat2, Vec3D>() {

		@Override
		public Vec3D convert (final ReadOnlyFloat2 input) {
			return new Vec3D(input.getX(), input.getY(), 0);
		}
	};
	private List<Vec3D> computed;

	@Override
	public void computeSpline (final Collection<? extends ReadOnlyFloat2> input, final int resolution) {
		final EditableCollection<Vec3D> O = Collections.newList();
		final Collection<ReadOnlyFloat2> I = (Collection<ReadOnlyFloat2>)input;
		Collections.convertCollection(I, O, this.converter);
		final Spline3D spline = new Spline3D(O);
		this.computed = spline.computeVertices(resolution);

	}

	@Override
	public void readValue (final double t, final Float2 result) {
		final long i = (long)(t * this.computed.size());
		final Vec3D value = this.computed.getElementAt(i);
		result.setX(value.x);
		result.setY(value.y);
	}

}
