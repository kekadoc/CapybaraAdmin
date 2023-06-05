package com.kekadoc.project.capybara.admin.data.source.remote.model.converter

import com.kekadoc.project.capybara.admin.data.source.remote.model.RangeDto
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.server.common.converter.Converter

object RangeDtoConverter : Converter.Bidirectional<RangeDto, Range> {

    override fun convert(value: RangeDto): Range = Range(
        from = value.from,
        count = value.count,
        query = value.query,
    )

    override fun revert(value: Range): RangeDto = RangeDto(
        from = value.from,
        count = value.count,
        query = value.query,
    )

}