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

package net.imglib2.concatenate;

import java.util.List;
import java.util.ListIterator;

/**
 * Utilities for manipulating lists of (pre-)concatenable objects.
 *
 *
 * @author Tobias Pietzsch
 */
public class Util
{
	public static void print( final List< ? > concatenatables )
	{
		final ListIterator< ? > iterator = concatenatables.listIterator();
		System.out.print( " ====  " );

		while ( iterator.hasNext() )
		{
			final Object a = iterator.next();
			System.out.print( a );
			if ( iterator.hasNext() )
				System.out.print( " x " );
		}
		System.out.println();
	}

	/**
	 * Reduce a list of objects by iteratively pre/concatenating neighboring
	 * objects if they support it.
	 *
	 * @param objects
	 *            list of objects that will be reduced in place
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < T > void join( final List< T > objects )
	{
		// print( objects );
		int oldConcatenablesSize;
		do
		{
			oldConcatenablesSize = objects.size();
			if ( oldConcatenablesSize >= 2 )
			{
				final ListIterator< T > iterator = objects.listIterator();
				T c1 = null;
				T c2 = iterator.next();
				while ( iterator.hasNext() )
				{
					c1 = c2;
					c2 = iterator.next();
					if ( Concatenable.class.isInstance( c1 ) && ( ( Concatenable ) c1 ).getConcatenableClass().isInstance( c2 ) )
					{
						c2 = ( T ) ( ( Concatenable ) c1 ).concatenate( c2 );
						iterator.remove();
						iterator.previous();
						iterator.set( c2 );
						iterator.next();
					}
					else if ( PreConcatenable.class.isInstance( c2 ) && ( ( PreConcatenable ) c2 ).getPreConcatenableClass().isInstance( c1 ) )
					{
						c2 = ( T ) ( ( PreConcatenable ) c2 ).preConcatenate( c1 );
						iterator.previous();
						iterator.previous();
						iterator.remove();
						iterator.next();
						iterator.set( c2 );
					}
				}
			}
			// print( objects );
		}
		while ( oldConcatenablesSize != objects.size() );
	}
}
