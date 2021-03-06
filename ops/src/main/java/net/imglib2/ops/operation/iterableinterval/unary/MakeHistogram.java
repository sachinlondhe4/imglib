/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.iterableinterval.unary;

import net.imglib2.histogram.Histogram1d;
import net.imglib2.histogram.Real1dBinMapper;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Felix Schoenenberger (University of Konstanz)
 *
 * @param <T>
 */
public final class MakeHistogram< T extends RealType< T >> implements UnaryOutputOperation< Iterable< T >, Histogram1d<T> >
{

	int m_numBins = 0;

	public MakeHistogram()
	{
		this( -1 );
	}

	public MakeHistogram( int numBins )
	{
		m_numBins = numBins;
	}

	@Override
	public final Histogram1d<T> createEmptyOutput( Iterable< T > op )
	{
		T type =  op.iterator().next().createVariable();
		
		if (m_numBins <= 0) {
			return new Histogram1d<T>(new Real1dBinMapper<T>(type.getMinValue(), type.getMaxValue(), 256, false));
		} else {
			return new Histogram1d<T>(new Real1dBinMapper<T>(type.getMinValue(), type.getMaxValue(), m_numBins, false));
		}
	}

	@Override
	public final Histogram1d<T> compute( Iterable< T > op, Histogram1d<T> r )
	{
		r.resetCounters();
		r.addData(op);

		return r;
	}

	@Override
	public Histogram1d<T> compute( Iterable< T > op )
	{
		return compute( op, createEmptyOutput( op ) );
	}

	@Override
	public UnaryOutputOperation< Iterable< T >, Histogram1d<T> > copy()
	{
		return new MakeHistogram< T >( m_numBins );
	}
}
