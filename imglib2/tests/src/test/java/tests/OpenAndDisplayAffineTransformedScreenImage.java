package tests;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.util.Timer;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineReadable;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;
import Jama.Matrix;

public class OpenAndDisplayAffineTransformedScreenImage
{	
	final static public void main( final String[] args )
		throws ImgIOException
	{
		new ImageJ();
		
		final ImgOpener io = new ImgOpener();
		//final RandomAccessibleInterval< UnsignedShortType > img = io.openImg( "/home/saalfeld/phd/light-microscopy/presentation/saalfeld-05-05-4-DPX-05_L1_Sum.lsm", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		final RandomAccessibleInterval< UnsignedShortType > img = io.openImg( "/home/saalfeld/phd/light-microscopy/presentation/saalfeld-05-05-4-DPX-05_L1_Sum.0.tif", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		
		final double[][] matrix = new double[][]{
				{ 0.5, 0, 0, img.dimension( 0 ) * 0.25 },
				{ 0, 0.5, 0, img.dimension( 1 ) * 0.25 },
				{ 0, 0, 0.5, 0 }
		};
		final AffineTransform affine = new AffineTransform( new Matrix( matrix ) );
//		final AffineTransform3D affine = new AffineTransform3D();
//		affine.set( matrix[ 0 ][ 0 ], matrix[ 0 ][ 1 ], matrix[ 0 ][ 2 ], matrix[ 0 ][ 3 ], matrix[ 1 ][ 0 ], matrix[ 1 ][ 1 ], matrix[ 1 ][ 2 ], matrix[ 1 ][ 3 ], matrix[ 2 ][ 0 ], matrix[ 2 ][ 1 ], matrix[ 2 ][ 2 ], matrix[ 2 ][ 3 ] );
		
		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType> > interpolatorFactory = new NearestNeighborInterpolatorFactory< UnsignedShortType >();
//		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType> > interpolatorFactory = new NLinearInterpolatorFactory< UnsignedShortType >();
		
		final RandomAccessible< UnsignedShortType > extendedImg = Views.extendValue( img, new UnsignedShortType() );
		//final RandomAccessible< UnsignedShortType > channel = Views.hyperSlice( img, 2, 0 );
		//final Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolant = new Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > >( channel, interpolatorFactory );
		final Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolant = new Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > >( extendedImg, interpolatorFactory );
//		final RealTransformRandomAccessible< UnsignedShortType, AffineTransform3D > mapping = new RealTransformRandomAccessible< UnsignedShortType, AffineTransform3D >( interpolant, affine );
//		final AffineRealRandomAccessible< UnsignedShortType, Affine > mapping = new AffineRealRandomAccessible< UnsignedShortType, Affine >( interpolant, affine );
		final AffineRandomAccessible< UnsignedShortType, AffineReadable > mapping = new AffineRandomAccessible< UnsignedShortType, AffineReadable >( interpolant, affine );
//		final ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D > mapping = new ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D >( interpolant, affine );
//		final RandomAccessibleOnRealRandomAccessible< UnsignedShortType > transformedPixels = new RandomAccessibleOnRealRandomAccessible< UnsignedShortType >( mapping );
		
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )img.dimension( 0 ), ( int )img.dimension( 1 ) );
//		final XYProjector< UnsignedShortType, ARGBType > projector = new XYProjector< UnsignedShortType, ARGBType >( transformedPixels, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
		final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( mapping, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//		final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( transformedPixels, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//		final XYProjector< UnsignedShortType, ARGBType > projector = new XYProjector< UnsignedShortType, ARGBType >( channel, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
		
		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		
		final Timer timer = new Timer();
		
		for ( int k = 0; k < 0; ++k )
		{
			timer.start();
//			final long last = img.dimension( 3 ) * 2 - 2;
			final long last = img.dimension( 2 ) * 2 - 2;
			for ( int i = 0; i < last; ++i )
			{
				projector.setPosition( i, 2 );
				projector.map();
				final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
				imp.setProcessor( cpa );
				imp.updateAndDraw();
			}
			IJ.log( "loop " + ( k + 1 ) + ": " + timer.stop() );
		}
		
		projector.setPosition( img.dimension( 2 ) / 2, 2 );
		
		final AffineTransform3D forward = new AffineTransform3D();
		final AffineTransform3D rotation = new AffineTransform3D();
		final AffineTransform3D scale = new AffineTransform3D();
		scale.set(
				2, 0, 0, 0,
				0, 2, 0, 0,
				0, 0, 2, 0 );
		
		for ( int k = 0; k < 3; ++k )
		{	
			timer.start();
			for ( int i = 0; i < 360; ++i )
			{
				rotation.rotate( 1, Math.PI / 360 );
				forward.set(
						1.0, 0, 0, -img.dimension( 0 ) / 2.0,
						0, 1.0, 0, -img.dimension( 1 ) / 2.0,
						0, 0, 1.0, -img.dimension( 2 ) / 2.0 );
				forward.preConcatenate( scale );
				forward.preConcatenate( rotation );
				forward.set(
						forward.get( 0, 0 ), forward.get( 0, 1 ), forward.get( 0, 2 ), forward.get( 0, 3 ) + img.dimension( 0 ) / 2.0,
						forward.get( 1, 0 ), forward.get( 1, 1 ), forward.get( 1, 2 ), forward.get( 1, 3 ) + img.dimension( 1 ) / 2.0,
						forward.get( 2, 0 ), forward.get( 2, 1 ), forward.get( 2, 2 ), forward.get( 2, 3 ) + img.dimension( 2 ) / 2.0 );
				
				affine.set( forward.inverse() );
				
//				final ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D > mapping1 = new ConstantAffineRandomAccessible< UnsignedShortType, AffineTransform3D >( interpolant, affine );
//				final XYRandomAccessibleProjector< UnsignedShortType, ARGBType > projector1 = new XYRandomAccessibleProjector< UnsignedShortType, ARGBType >( mapping1, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//				projector1.setPosition( img.dimension( 2 ) / 2, 2 );
//				
				//System.out.println( affine );
				
				projector.map();
				final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
				imp.setProcessor( cpa );
				imp.updateAndDraw();
			}
			IJ.log( "loop " + ( k + 1 ) + ": " + timer.stop() );
		}
		
		final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
		imp.setProcessor( cpa );
		imp.updateAndDraw();
	}
}