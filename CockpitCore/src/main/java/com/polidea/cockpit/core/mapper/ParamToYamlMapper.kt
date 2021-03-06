package com.polidea.cockpit.core.mapper

import com.polidea.cockpit.core.CockpitParam
import com.polidea.cockpit.core.isSimpleParam
import com.polidea.cockpit.core.type.*

internal class ParamToYamlMapper {

    fun toYamlMap(params: List<CockpitParam<Any>>): Map<String, Any> {
        return linkedMapOf(*params.map {
            if (it.isSimpleParam())
                toSimpleYamlFormat(it)
            else
                toExtendedYamlFormat(it)

        }.toTypedArray())
    }

    private fun toSimpleYamlFormat(it: CockpitParam<Any>) = Pair(it.name, it.value)

    private fun toExtendedYamlFormat(it: CockpitParam<Any>): Pair<String, LinkedHashMap<String, Any>> {
        val map = linkedMapOf<String, Any>()
        val value = it.value
        when (value) {
            is CockpitAction -> {
                map[MapperConsts.KEY_TYPE] = YamlParamType.ACTION.value
                value.buttonText?.let { map[MapperConsts.KEY_ACTION_BUTTON_TEXT] = it }
            }
            is CockpitListType<*> -> {
                map[MapperConsts.KEY_TYPE] = YamlParamType.LIST.value
                map[MapperConsts.KEY_LIST_VALUES] = value.items
                map[MapperConsts.KEY_LIST_SELECTION_INDEX] = value.selectedIndex
            }
            is CockpitColor -> {
                map[MapperConsts.KEY_TYPE] = YamlParamType.COLOR.value
                map[MapperConsts.KEY_VALUE] = value.value
            }
            is CockpitRange<*> -> {
                map[MapperConsts.KEY_TYPE] = YamlParamType.RANGE.value
                map[MapperConsts.KEY_MIN] = value.min
                map[MapperConsts.KEY_MAX] = value.max
                map[MapperConsts.KEY_STEP] = value.step
                map[MapperConsts.KEY_VALUE] = value.value
            }
            is CockpitStep<*> -> {
                value.min?.let { map[MapperConsts.KEY_MIN] = it }
                value.max?.let { map[MapperConsts.KEY_MAX] = it }
                map[MapperConsts.KEY_STEP] = value.step
                map[MapperConsts.KEY_VALUE] = value.value
            }
            is CockpitReadOnly -> {
                map[MapperConsts.KEY_TYPE] = YamlParamType.READ_ONLY.value
            }

            else -> value.let { map[MapperConsts.KEY_VALUE] = it }
        }
        it.description?.let { map[MapperConsts.KEY_DESCRIPTION] = it }
        it.group?.let { map[MapperConsts.KEY_GROUP] = it }
        return Pair(it.name, map)
    }
}
