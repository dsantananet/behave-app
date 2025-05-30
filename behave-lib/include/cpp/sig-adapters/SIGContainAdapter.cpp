/******************************************************************************
 *
 * Project:  CodeBlocks
 * Purpose:  Interface for SIGContain to be used in Behave-like applications
 *           used to tie together the classes used in a Contain simulation
 * Authors:  William Chatham <wchatham@fs.fed.us>
 *           Richard Sheperd <rsheperd@sig-gis.com>
 *
 *******************************************************************************
 *
 * THIS SOFTWARE WAS DEVELOPED AT THE ROCKY MOUNTAIN RESEARCH STATION (RMRS)
 * MISSOULA FIRE SCIENCES LABORATORY BY EMPLOYEES OF THE FEDERAL GOVERNMENT
 * IN THE COURSE OF THEIR OFFICIAL DUTIES. PURSUANT TO TITLE 17 SECTION 105
 * OF THE UNITED STATES CODE, THIS SOFTWARE IS NOT SUBJECT TO COPYRIGHT
 * PROTECTION AND IS IN THE PUBLIC DOMAIN. RMRS MISSOULA FIRE SCIENCES
 * LABORATORY ASSUMES NO RESPONSIBILITY WHATSOEVER FOR ITS USE BY OTHER
 * PARTIES,  AND MAKES NO GUARANTEES, EXPRESSED OR IMPLIED, ABOUT ITS QUALITY,
 * RELIABILITY, OR ANY OTHER CHARACTERISTIC.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 ******************************************************************************/

#include "SIGContainAdapter.h"
#include "SIGContainAdapter.h"
#define _USE_MATH_DEFINES
#include <math.h>

void SIGContainAdapter::addResource(double arrival,
                                    TimeUnits::TimeUnitsEnum arrivalTimeUnits,
                                    double duration,
                                    TimeUnits::TimeUnitsEnum durationTimeUnits,
                                    double productionRate,
                                    SpeedUnits::SpeedUnitsEnum productionRateUnits,
                                    const char * description,
                                    double baseCost,
                                    double hourCost)
{
    double arrivalInMinutes = TimeUnits::toBaseUnits(arrival, arrivalTimeUnits);
    double durationInMinutes = TimeUnits::toBaseUnits(duration, durationTimeUnits);
    ContainAdapter::addResource(arrivalInMinutes,
                                durationInMinutes,
                                TimeUnits::Minutes,
                                productionRate,
                                productionRateUnits,
                                std::string(description),
                                baseCost,
                                hourCost);
}

int SIGContainAdapter::removeResourceWithThisDesc(const char * desc)
{
  return ContainAdapter::removeResourceWithThisDesc(std::string(desc));
}

int SIGContainAdapter::removeAllResourcesWithThisDesc(const char * desc)
{
  return force_.removeAllResourcesWithThisDesc(desc);
}

double SIGContainAdapter::getPerimeterAtInitialAttack(LengthUnits::LengthUnitsEnum lengthUnits)
{
  return ContainAdapter::getPerimiterAtInitialAttack(lengthUnits);
}

DoubleVector SIGContainAdapter::getFirePerimeterX( void ) const
{
  return( DoubleVector(m_x, m_size) );
}

DoubleVector SIGContainAdapter::getFirePerimeterY( void ) const
{
  return( DoubleVector(m_y, m_size) );
}

int SIGContainAdapter::getFirePerimeterPointCount( void ) const
{
  return( m_size );
}


double SIGContainAdapter::getFireHeadAtReport( void ) const
{
  return ( m_reportHead );
}

double SIGContainAdapter::getFireBackAtReport( void ) const
{
  return ( m_reportBack );
}

double SIGContainAdapter::getFireHeadAtAttack( void ) const
{
  return ( m_attackHead );
}

double SIGContainAdapter::getFireBackAtAttack( void ) const
{
  double v = m_attackBack;
  return (v);
}

double SIGContainAdapter::getLengthToWidthRatio ( void ) const
{
  return ( lwRatio_ );
}

double SIGContainAdapter::getAttackDistance ( LengthUnits::LengthUnitsEnum lengthUnits ) const
{
  return ( LengthUnits::toBaseUnits(attackDistance_, lengthUnits));
}

double SIGContainAdapter::getReportSize( AreaUnits::AreaUnitsEnum areaUnits ) const
{
  double reportSizeInSquareFeet = AreaUnits::toBaseUnits(reportSize_, AreaUnits::Acres); // convert report size to base units
  return ( AreaUnits::fromBaseUnits(reportSizeInSquareFeet, areaUnits));
}

double SIGContainAdapter::getReportRate( SpeedUnits::SpeedUnitsEnum speedUnits ) const
{
  double reportRateInFeetPerMinute = SpeedUnits::toBaseUnits(reportRate_, SpeedUnits::ChainsPerHour); // convert report rate to base units
  return ( SpeedUnits::fromBaseUnits(reportRateInFeetPerMinute, speedUnits));
}

int SIGContainAdapter::getTactic( void ) const
{
  return ( tactic_ );
}
