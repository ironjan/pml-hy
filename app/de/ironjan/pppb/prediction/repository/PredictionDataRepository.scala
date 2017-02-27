package de.ironjan.pppb.prediction.repository

import de.ironjan.pppb.core.BaseRepository
import de.ironjan.pppb.prediction.model.PredictionResult
import slick.lifted.TableQuery

class PredictionDataRepository extends BaseRepository[PredictionDataTable, PredictionResult](TableQuery[PredictionDataTable])
